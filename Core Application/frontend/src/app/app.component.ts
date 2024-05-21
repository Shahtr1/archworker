import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { GenerateAppComponent } from './generate-app/generate-app.component';

@Component({
  selector: 'arch-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, GenerateAppComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'frontend';
}
