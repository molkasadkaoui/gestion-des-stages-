import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { StageList } from './components/stage-list/stage-list';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, StageList],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('gestion-stages-frontend');
}
