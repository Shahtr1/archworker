import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateAppComponent } from './generate-app.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

describe('GenerateAppComponent', () => {
  let component: GenerateAppComponent;
  let fixture: ComponentFixture<GenerateAppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateAppComponent, HttpClientTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(GenerateAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Layout', () => {
    it('has backend select input', () => {
      const app = fixture.nativeElement as HTMLElement;
      const label = app.querySelector('label[for="backend"]');
      const input = app.querySelector('select[id="backend"]');
      expect(input).toBeTruthy();
      expect(label).toBeTruthy();
      expect(label?.textContent?.trim()).toContain('Choose Backend Language:');
    });

    it('backend should have options Java, Node, Laravel', () => {
      const app = fixture.nativeElement as HTMLElement;
      const options = app.querySelectorAll('select[id="backend"] option');
      const optionValues = Array.from(options).map((option) =>
        option.textContent?.trim(),
      );
      expect(optionValues).toEqual(['Java', 'Node', 'Laravel']);
    });

    it('has frontend select input', () => {
      const app = fixture.nativeElement as HTMLElement;
      const label = app.querySelector('label[for="frontend"]');
      const input = app.querySelector('select[id="frontend"]');
      expect(input).toBeTruthy();
      expect(label).toBeTruthy();
      expect(label?.textContent?.trim()).toContain('Choose Frontend Language:');
    });

    it('frontend should have options Angular, React, Vue', () => {
      const app = fixture.nativeElement as HTMLElement;
      const options = app.querySelectorAll('select[id="frontend"] option');
      const optionValues = Array.from(options).map((option) =>
        option.textContent?.trim(),
      );
      expect(optionValues).toEqual(['Angular', 'React', 'Vue']);
    });

    it('has model input', () => {
      const app = fixture.nativeElement as HTMLElement;
      const label = app.querySelector('label[for="model"]');
      const input = app.querySelector('input[id="model"]');
      expect(input).toBeTruthy();
      expect(label).toBeTruthy();
      expect(label?.textContent?.trim()).toContain('Enter Model Name:');
    });

    it('has Generate Application button', () => {
      const app = fixture.nativeElement as HTMLElement;
      const button = app.querySelector('button');
      expect(button?.textContent?.trim()).toBe('Generate Application');
    });

    it('disables the button initially', () => {
      const app = fixture.nativeElement as HTMLElement;
      const button = app.querySelector('button');
      expect(button?.disabled).toBeTruthy();
    });
  });

  describe('Interactions', () => {
    let button: any;
    let httpTestingController: HttpTestingController;
    let app: HTMLElement;
    const setupForm = async () => {
      httpTestingController = TestBed.inject(HttpTestingController);

      app = fixture.nativeElement as HTMLElement;

      const backendSelectInput = app.querySelector(
        'select[id="backend"]',
      ) as HTMLSelectElement;

      const frontendSelectInput = app.querySelector(
        'select[id="frontend"]',
      ) as HTMLSelectElement;

      const modelInput = app.querySelector(
        'input[id="model"]',
      ) as HTMLSelectElement;

      backendSelectInput.value = 'Java';
      backendSelectInput.dispatchEvent(new Event('change'));

      frontendSelectInput.value = 'Angular';
      frontendSelectInput.dispatchEvent(new Event('change'));
      frontendSelectInput.dispatchEvent(new Event('blur'));

      modelInput.value = 'Customer';
      modelInput.dispatchEvent(new Event('input'));
      modelInput.dispatchEvent(new Event('blur'));

      fixture.detectChanges();

      button = app.querySelector('button');
    };

    it('enables the button when all the fields have valid input ', async function () {
      await setupForm();
      expect(button?.disabled).toBeFalsy();
    });
  });
});
