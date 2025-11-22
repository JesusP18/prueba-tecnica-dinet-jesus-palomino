package com.dinet.pedidos.infrastructure.config;

import com.dinet.pedidos.infrastructure.entities.ClienteEntity;
import com.dinet.pedidos.infrastructure.entities.ZonaEntity;
import com.dinet.pedidos.infrastructure.repositories.ClienteJpaRepository;
import com.dinet.pedidos.infrastructure.repositories.ZonaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner loadData(ClienteJpaRepository clienteRepository, 
                                     ZonaJpaRepository zonaRepository) {
        return args -> {
            // Cargar clientes de prueba
            if (clienteRepository.count() == 0) {
                logger.info("Cargando clientes de prueba...");
                clienteRepository.save(new ClienteEntity("CLI-123", true));
                clienteRepository.save(new ClienteEntity("CLI-456", true));
                clienteRepository.save(new ClienteEntity("CLI-789", true));
                clienteRepository.save(new ClienteEntity("CLI-999", true));
                clienteRepository.save(new ClienteEntity("CLI-001", true));
            }

            // Cargar zonas de prueba
            if (zonaRepository.count() == 0) {
                logger.info("Cargando zonas de prueba...");
                zonaRepository.save(new ZonaEntity("ZONA1", true));
                zonaRepository.save(new ZonaEntity("ZONA2", false));
                zonaRepository.save(new ZonaEntity("ZONA3", true));
                zonaRepository.save(new ZonaEntity("ZONA5", false));
                zonaRepository.save(new ZonaEntity("ZONA9", true));
            }

            logger.info("Datos de prueba cargados exitosamente");
        };
    }
}