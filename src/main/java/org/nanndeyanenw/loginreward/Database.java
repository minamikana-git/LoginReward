package org.nanndeyanenw.loginreward;


// インポート部分
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// データベースクラスの定義
public class Database {
    private Connection connection; // データベースへの接続
    private String url; // データベース接続URL
    public String dbPath; // データベースのパス

    // コンストラクタ
    public Database(String filename) {
        this.dbPath = filename; // ファイル名の設定
        this.url = "jdbc:sqlite:" + filename; // 接続URLの設定

        // データベースに接続
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // データベース接続メソッド
    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    // データベース切断メソッド
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // テーブル初期化メソッド
    public void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data ("
                + "uuid TEXT PRIMARY KEY,"
                + "days INTEGER,"
                + "last_login_date TEXT"
                + ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // プレイヤーデータ保存メソッド
    public void savePlayerData(UUID uuid, int days, String lastLoginDate) {
        String sql = "INSERT OR REPLACE INTO player_data (uuid, days, lastLoginDate) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, days);
            pstmt.setString(3, lastLoginDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // プレイヤーのログイン日数を取得するメソッド
    public int getLoginDays(UUID uuid) {
        String sql = "SELECT days FROM player_data WHERE uuid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("days");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 最後に受け取った日を取得するメソッド
    public String getLastReceivedDate(UUID uniqueId) {
        String lastReceivedDate = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT last_login_date FROM player_data WHERE uuid = ?");
            ps.setString(1, uniqueId.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lastReceivedDate = rs.getString("last_login_date");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lastReceivedDate;
    }

    // プレイヤーデータを取得するメソッド
    public Map<String, Object> getPlayerData(UUID playerUUID) {
        Map<String, Object> playerDataMap = new HashMap<>();

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");
            ps.setString(1, playerUUID.toString());
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                playerDataMap.put("uuid", resultSet.getString("uuid"));
                playerDataMap.put("lastReceived", resultSet.getString("lastReceived"));
                playerDataMap.put("daysLoggedIn", resultSet.getInt("daysLoggedIn"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerDataMap;
    }
}
