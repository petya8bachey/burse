package org.petya8bachey.dataloader;

import jakarta.transaction.Transactional;
import org.petya8bachey.enums.*;
import org.petya8bachey.model.*;
import org.petya8bachey.model.Transaction;
import org.petya8bachey.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // NEW
import org.springframework.security.crypto.password.PasswordEncoder; // NEW
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final BrokerRepository brokerRepository;
    private final ClientRepository clientRepository;
    private final StockRepository stockRepository;
    private final SessionRepository sessionRepository;
    private final RepositoryRepository repositoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository; // NEW
    private final PasswordEncoder passwordEncoder; // NEW

    public DataLoader(BrokerRepository brokerRepository, ClientRepository clientRepository,
                      StockRepository stockRepository, SessionRepository sessionRepository,
                      RepositoryRepository repositoryRepository, TransactionRepository transactionRepository,
                      UserRepository userRepository, // NEW
                      PasswordEncoder passwordEncoder) { // NEW
        this.brokerRepository = brokerRepository;
        this.clientRepository = clientRepository;
        this.stockRepository = stockRepository;
        this.sessionRepository = sessionRepository;
        this.repositoryRepository = repositoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository; // NEW
        this.passwordEncoder = passwordEncoder; // NEW
    }

    @Override
    @Transactional // Важно: все операции внутри должны быть в одной транзакции
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже данные, чтобы не добавлять их при каждом перезапуске
        if (brokerRepository.count() == 0) {
            System.out.println("Loading initial data...");

            // NEW: Create Admin User
            createUser("admin", "adminpass", UserRole.ADMIN, null, null);

            // 1. Создание брокеров и их пользователей
            User broker1User = createUser("broker1", "brokerpass", UserRole.BROKER, null, null);
            Broker broker1 = createBroker("LIC-BROKER-001", "Alpha Brokerage Inc.", BrokerStatus.ACTIVE, broker1User);
            broker1User.setBrokerProfile(broker1); // Link user to broker profile
            userRepository.save(broker1User); // Save user to cascade changes

            User broker2User = createUser("broker2", "brokerpass", UserRole.BROKER, null, null);
            Broker broker2 = createBroker("LIC-BROKER-002", "Beta Investments LLC", BrokerStatus.ACTIVE, broker2User);
            broker2User.setBrokerProfile(broker2);
            userRepository.save(broker2User);

            // 2. Создание акций
            Stock stockAAPL = createStock("AAPL", "Apple Inc.", StockSector.TECH, new BigDecimal("175.50"));
            Stock stockMSFT = createStock("MSFT", "Microsoft Corp.", StockSector.TECH, new BigDecimal("420.00"));
            Stock stockSBER = createStock("SBER", "Sberbank", StockSector.FINANCE, new BigDecimal("300.00"));

            // 3. Регистрация акций у брокеров (М:М)
            broker1.getRegisteredStocks().add(stockAAPL);
            broker1.getRegisteredStocks().add(stockMSFT);
            brokerRepository.save(broker1); // Сохраняем, чтобы обновить связь

            broker2.getRegisteredStocks().add(stockSBER);
            broker2.getRegisteredStocks().add(stockAAPL); // Одна акция может быть у разных брокеров
            brokerRepository.save(broker2);

            // 4. Создание клиентов и их пользователей
            User client1User = createUser("client1", "clientpass", UserRole.CLIENT, null, null);
            Client client1 = createClient("Иванов Иван Иванович", "123456789012", ClientType.PHYSICAL, LocalDate.now().minusMonths(6), broker1, client1User);
            client1User.setClientProfile(client1);
            userRepository.save(client1User);

            User client2User = createUser("client2", "clientpass", UserRole.CLIENT, null, null);
            Client client2 = createClient("ООО \"Рога и Копыта\"", "987654321098", ClientType.LEGAL, LocalDate.now().minusMonths(3), broker1, client2User);
            client2User.setClientProfile(client2);
            userRepository.save(client2User);

            User client3User = createUser("client3", "clientpass", UserRole.CLIENT, null, null);
            Client client3 = createClient("Петров Петр Петрович", "555444333222", ClientType.PHYSICAL, LocalDate.now().minusMonths(1), broker2, client3User);
            client3User.setClientProfile(client3);
            userRepository.save(client3User);

            // 5. Клиенты торгуют акциями (М:М)
            client1.getTradedStocks().add(stockAAPL);
            client1.getTradedStocks().add(stockSBER);
            clientRepository.save(client1);

            client2.getTradedStocks().add(stockMSFT);
            clientRepository.save(client2);

            client3.getTradedStocks().add(stockAAPL);
            client3.getTradedStocks().add(stockMSFT);
            clientRepository.save(client3);

            // 6. Создание торговых сессий
            Session sessionToday = createSession(LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(6), SessionStatus.ACTIVE);
            Session sessionYesterday = createSession(LocalDateTime.now().minusDays(1).withHour(9).withMinute(0), LocalDateTime.now().minusDays(1).withHour(18).withMinute(0), SessionStatus.CLOSED);

            // 7. Клиенты участвуют в сессиях (М:М)
            client1.getParticipatingSessions().add(sessionToday);
            client1.getParticipatingSessions().add(sessionYesterday);
            clientRepository.save(client1);

            client2.getParticipatingSessions().add(sessionToday);
            clientRepository.save(client2);

            client3.getParticipatingSessions().add(sessionToday);
            clientRepository.save(client3);

            // 8. Создание репозитория
            org.petya8bachey.model.Repository mainRepo = createRepository(250);

            // 9. Создание сделок
            createTransaction(client1, stockAAPL, sessionToday, mainRepo, new BigDecimal("175.00"), 10, TransactionDirection.BUY);
            createTransaction(client2, stockMSFT, sessionToday, mainRepo, new BigDecimal("419.80"), 5, TransactionDirection.SELL);
            createTransaction(client1, stockSBER, sessionYesterday, mainRepo, new BigDecimal("299.50"), 20, TransactionDirection.BUY);
            createTransaction(client3, stockAAPL, sessionToday, mainRepo, new BigDecimal("175.60"), 3, TransactionDirection.SELL);

            System.out.println("Initial data loaded successfully!");
        } else {
            System.out.println("Database already contains data. Skipping initial data load.");
        }
    }

    // --- Вспомогательные методы для создания сущностей ---

    // NEW: Helper method for creating User
    private User createUser(String username, String password, UserRole role, Broker brokerProfile, Client clientProfile) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password)); // Hash the password
        user.setRole(role);
        user.setEnabled(true);
        user.setBrokerProfile(brokerProfile); // Set profile if available
        user.setClientProfile(clientProfile); // Set profile if available
        return userRepository.save(user);
    }

    // UPDATED: createBroker now accepts a User
    private Broker createBroker(String licenseNumber, String companyName, BrokerStatus status, User user) {
        Broker broker = new Broker();
        broker.setLicenseNumber(licenseNumber);
        broker.setCompanyName(companyName);
        broker.setStatus(status);
        broker.setUser(user); // Link user to broker
        return brokerRepository.save(broker);
    }

    private Stock createStock(String stockId, String companyName, StockSector sector, BigDecimal currentPrice) {
        Stock stock = new Stock();
        stock.setStockId(stockId);
        stock.setCompanyName(companyName);
        stock.setSector(sector);
        stock.setCurrentPrice(currentPrice);
        return stockRepository.save(stock);
    }

    // UPDATED: createClient now accepts a User
    private Client createClient(String fullName, String taxId, ClientType clientType, LocalDate registrationDate, Broker broker, User user) {
        Client client = new Client();
        client.setFullName(fullName);
        client.setTaxId(taxId);
        client.setClientType(clientType);
        client.setRegistrationDate(registrationDate);
        client.setBroker(broker);
        client.setUser(user); // Link user to client
        return clientRepository.save(client);
    }

    private Session createSession(LocalDateTime startTime, LocalDateTime endTime, SessionStatus status) {
        Session session = new Session();
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setStatus(status);
        return sessionRepository.save(session);
    }

    private org.petya8bachey.model.Repository createRepository(int storageSizeGb) {
        org.petya8bachey.model.Repository repo = new org.petya8bachey.model.Repository();
        repo.setStorageSizeGb(storageSizeGb);
        return repositoryRepository.save(repo);
    }

    private Transaction createTransaction(Client client, Stock stock, Session session, org.petya8bachey.model.Repository repository,
                                          BigDecimal price, int volume, TransactionDirection direction) {
        Transaction tx = new Transaction();
        tx.setClient(client);
        tx.setStock(stock);
        tx.setSession(session);
        tx.setRepository(repository);
        tx.setPrice(price);
        tx.setVolume(volume);
        tx.setDirection(direction);
        return transactionRepository.save(tx);
    }
}
