package com.blockchain.cliente.blockchaincliente.config;


import com.blockchain.cliente.blockchaincliente.user.FabricUserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class NetworkConfigBlockchain {

    @Value("classpath:connectiondetails/connection-profile-cloud.json")
    private transient Resource connectionFile;

    /**
     * metodo cria uma conexao com a cloud ou local hyperledger network
     * @return networkConfig
     */
    @Bean
    public NetworkConfig createNetworkConfig(){
        NetworkConfig networkConfig =null;
        try {
            File connectionProfile = connectionFile.getFile();
            networkConfig = NetworkConfig.fromJsonFile(connectionProfile);
        } catch (IOException | InvalidArgumentException | NetworkConfigurationException ex) {
            Logger.getLogger(NetworkConfigBlockchain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return networkConfig;
    }

    @Bean
    public HFCAClient createCAClient() throws Exception {

        NetworkConfig networkConfig = createNetworkConfig();
        NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
        NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

        //Certificado de Autoridade do cliente
        HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfcaClient.setCryptoSuite(cryptoSuite);

        return hfcaClient;
    }

    @Bean(name = "AdminUserContext")
    public FabricUserContext enrollAdmin() throws Exception {

        HFCAClient hfcaClient = createCAClient();

        FabricUserContext adminUserContext = new FabricUserContext();
        adminUserContext.setName(BlockchainNetworkAttributes.ADMIN_NAME);
        adminUserContext.setAffiliation(BlockchainNetworkAttributes.ORG1_NAME);
        Set<String> roles = new HashSet<>();
        roles.add("member");
        roles.add("admin");
        adminUserContext.setRoles(roles);
        adminUserContext.setMspId(BlockchainNetworkAttributes.ORG1_MSP);
        Enrollment adminEnrollment = hfcaClient.enroll(
                BlockchainNetworkAttributes.ADMIN_NAME,
                BlockchainNetworkAttributes.ADMIN_PASSWORD);
        adminUserContext.setEnrollment(adminEnrollment);

        return adminUserContext;
    }

    @Bean
    public HFClient createHfClient() throws Exception {
        FabricUserContext userContext = enrollAdmin();
        HFClient hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfClient.setCryptoSuite(cryptoSuite);
        hfClient.setUserContext(userContext);
        return hfClient;
    }

    @Bean(name = "channel1")
    public Channel createChannel1() throws Exception {
        HFClient hfClient = createHfClient();
        Channel newChannel = hfClient.loadChannelFromConfig(
                BlockchainNetworkAttributes.CHANNEL_1_NAME,
                createNetworkConfig());
        if (newChannel == null){
            throw new RuntimeException(
                    "Channel " + BlockchainNetworkAttributes.CHANNEL_1_NAME
                            + " is not defined in the config file!");
        }
        return newChannel.initialize();
    }

    @Bean
    public ObjectMapper objectMapper(){
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
