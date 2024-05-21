import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgForOf } from '@angular/common';

@Component({
  selector: 'arch-generate-app',
  standalone: true,
  imports: [ReactiveFormsModule, NgForOf],
  templateUrl: './generate-app.component.html',
  styleUrl: './generate-app.component.scss',
})
export class GenerateAppComponent {
  generateForm: FormGroup;

  backendOptions = ['Java', 'Node', 'Laravel'];
  frontendOptions = ['Angular', 'React', 'Vue'];

  constructor(private fb: FormBuilder) {
    this.generateForm = this.fb.group({
      backend: [null, [Validators.required]],
      frontend: [null, [Validators.required]],
      model: [null, [Validators.required]],
    });
  }

  onSubmit() {}
}
