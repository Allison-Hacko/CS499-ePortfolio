import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TripCard } from './trip-card';
import { TripData } from '../trip';

describe('TripCard', () => {
  let component: TripCard;
  let fixture: ComponentFixture<TripCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripCard]
    }).compileComponents();

    fixture = TestBed.createComponent(TripCard);
    component = fixture.componentInstance;
    component.trip = {
      code: 'TEST',
      name: 'Test Getaway',
      length: '7 days',
      start: '2026-11-01',
      resort: 'Test Resort',
      perPerson: '999',
      image: '',
      description: 'A test trip'
    } as TripData;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return an empty image URL when no image is supplied', () => {
    expect(component.imageUrl).toBe('');
  });
});
