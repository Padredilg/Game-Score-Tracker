import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaderboardList } from './leaderboard-list';

describe('LeaderboardList', () => {
  let component: LeaderboardList;
  let fixture: ComponentFixture<LeaderboardList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaderboardList],
    }).compileComponents();

    fixture = TestBed.createComponent(LeaderboardList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
