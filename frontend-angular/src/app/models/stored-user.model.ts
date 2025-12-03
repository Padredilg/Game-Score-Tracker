export interface StoredUser {
  username: string;
  nickname?: string;
  role: string;
  groupCode: string;
  cumulativeScore: number;
  highestScore: number;
  matchesPlayed: number;
  victories: number;
  defeats: number;
  userId?: string;
}
