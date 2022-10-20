package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(AvailableSettings.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(AvailableSettings.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(AvailableSettings.USER, "root");
        properties.put(AvailableSettings.PASS, "root");
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .setProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> query = session.createNativeQuery("select * from rpg.player", Player.class);
            return query.list();
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> query = session.createNamedQuery("Player_getAllCount", Integer.class);
            return query.uniqueResult();
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(player);
            transaction.commit();
            session.clear();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(player);
            transaction.commit();
            session.clear();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player = session.get(Player.class, id);
            return Optional.of(player);
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();
            session.clear();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}