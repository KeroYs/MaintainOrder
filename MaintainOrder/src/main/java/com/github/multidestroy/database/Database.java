package com.github.multidestroy.database;

import com.github.multidestroy.Config;
import com.github.multidestroy.info.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class Database {

    private final Config config;
    private DataSource dataSource;
    private final PlayerRank playerRank;
    private boolean connected;

    public Database(Config config, PlayerRank playerRank) {
        this.connected = false;
        this.config = config;
        this.playerRank = playerRank;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        dataSource.close();
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean reloadDataSource() {
        try {
            dataSource = new DataSource(config);
            return connected = true;
        } catch (Exception e) {
            Logger logger = ProxyServer.getInstance().getLogger();
            logger.severe(ChatColor.GOLD + "*********************************************************************************************");
            logger.severe(ChatColor.RED + "DataBase is not connected! Try to correct database values in plugins/MaintainOrder/config.yml");
            e.printStackTrace();
            logger.severe(ChatColor.GOLD + "*********************************************************************************************");
            return connected = false;
        }
    }

    public void saveDefaultTables() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Connection finalConn = conn;
            ProxyServer.getInstance().getServers().values().forEach(server -> {
                createBanTable(finalConn, server.getName());
            });

            createBanTable(conn, "blacklist");
            createHistoricBansTable(conn);
            createServersTable(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
        }

    }

    /**
     * @return TRUE - if ban was successfully saved, otherwise FALSE
     */

    public boolean saveBan(String serverName, CommandSender sender, String receiver, String reason, Instant now, Instant expiration) {
        Connection conn = null;
        String query = "INSERT INTO " + config.get().getSection("server").getString(serverName) +
                " VALUES (DEFAULT, ?, ?, ?, ?, ?)";
        boolean isBanSaved;
        try {
            conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, receiver);

            if (sender instanceof ProxiedPlayer)
                st.setInt(2, getPlayerId(conn, sender.getName())); //Player
            else
                st.setInt(2, -1); //Console

            st.setString(3, reason);
            st.setLong(4, now.toEpochMilli());

            if(expiration == null)
                st.setLong(5, Long.MAX_VALUE); //Perm
            else
                st.setLong(5, expiration.toEpochMilli());
            st.execute();
            isBanSaved = true;
        } catch (SQLException e) {
            e.printStackTrace();
            isBanSaved = false;
        } finally {
            closeConn(conn);
        }

        return isBanSaved;
    }

    public BanData getLastGivenOwnBan(String serverName, String playerName, String giverName) {
        Connection conn = null;
        BanData banData = null;
        String query = "SELECT * FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE giver=(SELECT id FROM players WHERE LOWER(nick)=?) " +
                "AND LOWER(recipient)=? AND time = (SELECT min(time) FROM " + config.get().getSection("server").getString(serverName) + ")";
        try {
            conn = dataSource.getConnection();

            PreparedStatement st = conn.prepareStatement( query );
            st.setString(1, giverName.toLowerCase());
            st.setString(2, playerName.toLowerCase());
            ResultSet rs = st.executeQuery();
            if(rs.next())
                banData = getBanData(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
        }

        return banData;
    }

    public BanData getLastGivenBan(String serverName, String playerName) {
        String query = "SELECT * FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE LOWER(recipient)=? AND time=(SELECT min(time) FROM " + config.get().getSection("server").getString(serverName) + ")";

        return getLastSpecifiedBan(playerName, query);
    }

    public BanData getLastExpiringBan(String serverName, String playerName) {
        String query = "SELECT * FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE LOWER(recipient)=? AND expiration=(SELECT max(expiration) FROM " + config.get().getSection("server").getString(serverName) + ")";
        return getLastSpecifiedBan(playerName, query);
    }

    public List<BanData> getOngoingBans(String serverName, String playerName) {
        Connection conn = null;
        List<BanData> ongoingBansList = new ArrayList<>();
        String query = "SELECT * FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE LOWER(recipient)=? ORDER BY time";
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, playerName.toLowerCase());
            ResultSet rs = ps.executeQuery();

            while(rs.next())
                ongoingBansList.add(getBanData(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
        }

        return ongoingBansList;
    }

    /**
     * @return -1: reached exception, 0: player is not banned, 1: player is banned
     */

    public int checkBan(String serverName, String playerName, String playerAddress) {
        Connection conn = null;
        int returnValue;
        String query = "SELECT id FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE LOWER(recipient)=? OR recipient=?";
        try {
            conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement( query );
            st.setString(1, playerName.toLowerCase());
            st.setString(2, playerAddress);
            ResultSet rs = st.executeQuery();
            if(rs.next())
                returnValue = 1;
            else returnValue = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            returnValue = -1;
        } finally {
            closeConn(conn);
        }
        return returnValue;
    }

    /**
     * @return -1: reached exception, 0: player is not banned, 1: player is banned
     */

    public int checkBan(String serverName, String playerName) {
        Connection conn = null;
        int returnValue;
        String query = "SELECT id FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE LOWER(recipient)=?";
        try {
            conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement( query );
            st.setString(1, playerName.toLowerCase());
            ResultSet rs = st.executeQuery();
            if(rs.next())
                returnValue = 1;
            else returnValue = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            returnValue = -1;
        } finally {
            closeConn(conn);
        }

        return returnValue;
    }

    /**
     * @return 1 - if player's ip is blockaded on account, 0 - if player's ip is not locked or -1 if caught exception
     */

    public int checkIpBlockade(String playerName, InetAddress address) {
        String query = "SELECT * FROM ip_blockades" +
                " WHERE player=(SELECT id FROM players WHERE LOWER(nick)=?)" +
                " AND ip_address=?";
        Connection conn = null;
        int returnValue;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement( query );
            ps.setString(1, playerName.toLowerCase());
            ps.setString(2, address.getHostAddress());

            if(ps.executeQuery().next())
                returnValue = 1;
            else
                returnValue = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            returnValue = -1;
        } finally {
            closeConn(conn);
        }

        return returnValue;
    }

    public Instant removeExpiredBans(String serverName, Instant now) {
        Connection conn = null;
        Instant returnInstant = null;
        String query = "SELECT * FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE expiration!=? ORDER BY expiration";
        try {
            conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement( query );
            st.setLong(1, Long.MAX_VALUE);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Instant expiration = Instant.ofEpochMilli(rs.getLong(6));
                if (expiration != null)
                    if (expiration.isBefore(now))
                        removeBan(null, serverName, getBanData(rs), ModificationType.EXPIRED, null);
                    else returnInstant = expiration;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
        }

        return returnInstant;
    }

    public boolean removeBan(CommandSender remover, String serverName, BanData banData, ModificationType modificationType, String reason) {
        Connection conn = null;
        boolean returnValue;
        String query = "DELETE FROM " + config.get().getSection("server").getString(serverName) +
                " WHERE id=?";
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            //delete from ban database
            PreparedStatement ps = conn.prepareStatement( query );
            ps.setInt(1, banData.id);
            ps.execute();

            //save in historic_bans database
            saveHistoricBan(conn, remover, serverName, banData, modificationType, reason);
            returnValue = true;
        } catch (SQLException e) {
            e.printStackTrace();
            returnValue = false;
        } finally {
            closeConn(conn);
        }

        return returnValue;
    }

    private void saveHistoricBan(Connection conn, CommandSender remover, String serverName, BanData banData, ModificationType modificationType, String reason) {
        try {
            short serverId = getServerId(conn, serverName);
            int removerId;
            if (remover instanceof ProxiedPlayer)
                removerId = getPlayerId(conn, remover.getName());
            else
                removerId = -1;

            StringBuilder extra = new StringBuilder();
            if (modificationType == ModificationType.EXPIRED)
                extra.append(modificationType.getValue());
            else {
                extra.append(modificationType.getValue()).append(";").append(removerId).append(";");
                if (reason != null) {
                    reason = reason.replaceAll(";", ":");
                    extra.append(reason).append(";").append(LocalDateTime.now());
                }
                else extra.append(";").append(LocalDateTime.now());
            }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO historic_bans VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, banData.id);
            ps.setString(2, banData.recipient);
            ps.setInt(3, banData.giverID);
            ps.setString(4, banData.reason);
            ps.setLong(5, banData.time);
            ps.setLong(6, banData.expiration);
            ps.setShort(7, serverId);
            ps.setString(8, extra.toString());

            ps.execute();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public String getPlayerName(int id) {
        if(id == -1)
            return "CONSOLE";
        Connection conn = null;
        String returnString;
        try {
            conn = dataSource.getConnection();
            PreparedStatement psServerId = conn.prepareStatement("SELECT nick FROM players WHERE id=?");
            psServerId.setInt(1, id);
            ResultSet rs = psServerId.executeQuery();
            if(rs.next())
                returnString = rs.getString(1);
            else returnString = "NOT FOUND";
        } catch (SQLException e) {
            e.printStackTrace();
            returnString = "ERROR";
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return returnString;
    }

    private byte getServerId(Connection conn, String serverName) throws SQLException {
        PreparedStatement psServerId = conn.prepareStatement("SELECT id FROM servers WHERE name=?");
        psServerId.setString(1, serverName);
        ResultSet rs = psServerId.executeQuery();
        if(rs.next())
            return rs.getByte(1);
        return -1;
    }

    private int getPlayerId(Connection conn, String playerName) throws SQLException {
        PreparedStatement st = conn.prepareStatement("SELECT id FROM players WHERE LOWER(nick)=?");
        st.setString(1, playerName.toLowerCase());
        ResultSet rs = st.executeQuery();
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }

    /**
     * @return null if caught an exception
     */

    public BaseComponent[] getPlayerRank(String playerName) {
        Connection conn = null;
        BaseComponent[] rank;
        try {
            conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT rank FROM players WHERE LOWER(nick)=?");
            st.setString(1, playerName.toLowerCase());
            ResultSet rs = st.executeQuery();
            if (rs.next())
                rank = playerRank.getRank(rs.getInt(1));
            else
                rank = playerRank.getNotExistRank();

        } catch (SQLException e) {
            e.printStackTrace();
            rank = null;
        } finally {
            closeConn(conn);
        }
        return rank;
    }

    public String getIpOfLastSuccessfulLogin(String playerName) {
        Connection conn = null;
        String ip = "";
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT a.ip_address FROM players AS p INNER JOIN activity_history AS a ON a.player=p.id" +
                    " WHERE LOWER(p.nick)=? AND (a.status=? OR a.status=?) ORDER BY a.id DESC LIMIT 1");
            ps.setString(1, playerName.toLowerCase());
            ps.setInt(2, PlayerActivityStatus.SUCCESSFUL_LOGIN.getId());
            ps.setInt(3, PlayerActivityStatus.REGISTRATION.getId());
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                ip = rs.getString(1);
            else
                ip = "No information";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConn(conn);
        return ip;
    }

    private BanData getLastSpecifiedBan(String playerName, String query) {
        Connection conn = null;
        BanData banData = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement( query );
            st.setString(1, playerName.toLowerCase());
            ResultSet rs = st.executeQuery();
            if(rs.next())
                banData = getBanData(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(conn);
        }

        return banData;
    }

    private BanData getBanData(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String recipient = rs.getString(2);
        int giver = rs.getInt(3);
        String reason = rs.getString(4);
        long time = rs.getLong(5);
        long expiration = rs.getLong(6);

        return new BanData(this, id, recipient, giver, reason, time, expiration);
    }

    private void createBanTable(Connection conn, String serverName) {
        try {
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS " + config.get().getSection("server").getString(serverName) + " ("
                    + "id SERIAL PRIMARY KEY,"
                    + "recipient VARCHAR(17),"
                    + "giver INT,"
                    + "reason VARCHAR(300),"
                    + "time BIGINT,"
                    + "expiration BIGINT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createHistoricBansTable(Connection conn) {
        try {
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS historic_bans("
                    + "id INT,"
                    + "recipient VARCHAR(17),"
                    + "giver INT,"
                    + "reason VARCHAR(300),"
                    + "time BIGINT,"
                    + "expiration BIGINT,"
                    + "type SMALLINT,"
                    + "extra VARCHAR(330))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createServersTable(Connection conn) {
        try {
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS servers("
                    + "id SERIAL PRIMARY KEY,"
                    + "name VARCHAR(30))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConn(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}