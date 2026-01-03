package com.example.fitness.model

data class LeaderboardEntry(
    val rank: Int,
    val userId: Int,
    val nickname: String,
    val avatarUrl: String?,
    val totalDistanceKm: Float,
    val totalCalories: Int,
    val totalTimeMinutes: Int
)

val sampleLeaderboard = listOf(
    LeaderboardEntry(1, 101, "Trần Văn A", "https://i.pravatar.cc/150?img=11", 187.4f, 5420, 1245),
    LeaderboardEntry(2, 102, "Nguyễn Thị Bích", "https://i.pravatar.cc/150?img=28", 162.8f, 4780, 1380),
    LeaderboardEntry(3, 103, "Lê Hoàng Cường", "https://i.pravatar.cc/150?img=45", 149.1f, 4310, 1105),
    LeaderboardEntry(4, 104, "Phạm Minh Đức", "https://i.pravatar.cc/150?img=62", 138.7f, 4025, 980),
    LeaderboardEntry(5, 105, "Vũ Ngọc Lan", "https://i.pravatar.cc/150?img=33", 129.3f, 3750, 920),
    LeaderboardEntry(6, 106, "Đặng Anh Tuấn", "https://i.pravatar.cc/150?img=19", 118.6f, 3440, 850),
    LeaderboardEntry(7, 107, "Hoàng Thị Mai", "https://i.pravatar.cc/150?img=40", 109.2f, 3170, 790),
    LeaderboardEntry(8, 108, "Bùi Quốc Huy", "https://i.pravatar.cc/150?img=57", 98.5f, 2860, 720),
    LeaderboardEntry(9, 109, "Trương Lan Anh", "https://i.pravatar.cc/150?img=24", 91.4f, 2650, 680),
    LeaderboardEntry(10, 110, "Mai Văn Nam", "https://i.pravatar.cc/150?img=36", 85.7f, 2480, 640)
)