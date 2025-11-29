export interface AddMatch {
  playerId?: string;
  opponent?: string;
  game?: string;
  myScore?: number;
  result?: 'win' | 'loss' | 'draw';
  playedAt?: Date;
  teammates?: string[];
  opponents?: string[];
  teammatesDetailed?: { name: string; score?: number }[];
  opponentsDetailed?: { name: string; score?: number }[];
  notes?: string;
}
