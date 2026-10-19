import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { TripListing } from './trip-listing';

describe('TripListing', () => {
  let component: TripListing;
  let fixture: ComponentFixture<TripListing>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripListing],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(TripListing);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const request = httpMock.expectOne('/api/trips');
    request.flush([]);
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the trip list', () => {
    expect(component.trips).toEqual([]);
    expect(component.errorMessage).toBe('');
  });
});
