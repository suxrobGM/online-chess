import {Component} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {DropdownChangeEvent, DropdownModule} from 'primeng/dropdown';
import {Theme, ThemeService} from '@chessmate-app/core/services';

@Component({
  selector: 'app-theme-switcher',
  standalone: true,
  templateUrl: './theme-switcher.component.html',
  imports: [DropdownModule, ReactiveFormsModule],
})
export class ThemeSwitcherComponent {
  public themes: Theme[];
  public selectedTheme: FormControl<Theme | null>;

  constructor(private readonly themeService: ThemeService) {
    this.themes = Array.from(this.themeService.getThemes());
    this.selectedTheme = new FormControl(this.themeService.getCurrentTheme());
  }

  switchTheme(event: DropdownChangeEvent) {
    this.themeService.switchTheme(event.value.name);
  }
}
