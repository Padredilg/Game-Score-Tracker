import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerLogin } from './player-login';

describe('PlayerLogin', () => {
  let component: PlayerLogin;
  let fixture: ComponentFixture<PlayerLogin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerLogin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlayerLogin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
