import {DOCUMENT} from '@angular/common';
import {Injectable, Inject} from '@angular/core';

@Injectable({providedIn: 'root'})
export class ThemeService {
  private readonly themeStorageKey = 'APP_THEME';
  private readonly defaultTheme = 'bootstrap4-dark-purple';
  private readonly themes = new Map<string, Theme>();
  private currentTheme: Theme;

  constructor(@Inject(DOCUMENT) private readonly document: Document) {
    this.themes.set('arya-blue', {name: 'arya-blue', displayName: 'Arya Blue'});
    this.themes.set('arya-green', {name: 'arya-green', displayName: 'Arya Green'});
    this.themes.set('arya-orange', {name: 'arya-orange', displayName: 'Arya Orange'});
    this.themes.set('arya-purple', {name: 'arya-purple', displayName: 'Arya Purple'});
    this.themes.set('bootstrap4-dark-blue', {name: 'bootstrap4-dark-blue', displayName: 'Bootstrap 4 Dark Blue'});
    this.themes.set('bootstrap4-dark-purple', {name: 'bootstrap4-dark-purple', displayName: 'Bootstrap 4 Dark Purple'});
    this.themes.set('bootstrap4-light-blue', {name: 'bootstrap4-light-blue', displayName: 'Bootstrap 4 Light Blue'});
    this.themes.set('bootstrap4-light-purple', {name: 'bootstrap4-light-purple', displayName: 'Bootstrap 4 Light Purple'});
    this.themes.set('fluent-light', {name: 'fluent-light', displayName: 'Fluent Light'});
    this.themes.set('lara-dark-amber', {name: 'lara-dark-amber', displayName: 'Lara Dark Amber'});
    this.themes.set('lara-dark-blue', {name: 'lara-dark-blue', displayName: 'Lara Dark Blue'});
    this.themes.set('lara-dark-cyan', {name: 'lara-dark-cyan', displayName: 'Lara Dark Cyan'});
    this.themes.set('lara-dark-green', {name: 'lara-dark-green', displayName: 'Lara Dark Green'});
    this.themes.set('lara-dark-indigo', {name: 'lara-dark-indigo', displayName: 'Lara Dark Indigo'});
    this.themes.set('lara-dark-pink', {name: 'lara-dark-pink', displayName: 'Lara Dark Pink'});
    this.themes.set('lara-dark-purple', {name: 'lara-dark-purple', displayName: 'Lara Dark Purple'});
    this.themes.set('lara-dark-teal', {name: 'lara-dark-teal', displayName: 'Lara Dark Teal'});
    this.themes.set('lara-light-amber', {name: 'lara-light-amber', displayName: 'Lara Light Amber'});
    this.themes.set('lara-light-blue', {name: 'lara-light-blue', displayName: 'Lara Light Blue'});
    this.themes.set('lara-light-cyan', {name: 'lara-light-cyan', displayName: 'Lara Light Cyan'});
    this.themes.set('lara-light-green', {name: 'lara-light-green', displayName: 'Lara Light Green'});
    this.themes.set('lara-light-indigo', {name: 'lara-light-indigo', displayName: 'Lara Light Indigo'});
    this.themes.set('lara-light-pink', {name: 'lara-light-pink', displayName: 'Lara Light Pink'});
    this.themes.set('lara-light-purple', {name: 'lara-light-purple', displayName: 'Lara Light Purple'});
    this.themes.set('lara-light-teal', {name: 'lara-light-teal', displayName: 'Lara Light Teal'});
    this.themes.set('luna-amber', {name: 'luna-amber', displayName: 'Luna Amber'});
    this.themes.set('luna-blue', {name: 'luna-blue', displayName: 'Luna Blue'});
    this.themes.set('luna-green', {name: 'luna-green', displayName: 'Luna Green'});
    this.themes.set('luna-pink', {name: 'luna-pink', displayName: 'Luna Pink'});
    this.themes.set('md-dark-deeppurple', {name: 'md-dark-deeppurple', displayName: 'MD Dark Deeppurple'});
    this.themes.set('md-dark-indigo', {name: 'md-dark-indigo', displayName: 'MD Dark Indigo'});
    this.themes.set('md-light-deeppurple', {name: 'md-light-deeppurple', displayName: 'MD Light Deeppurple'});
    this.themes.set('md-light-indigo', {name: 'md-light-indigo', displayName: 'MD Light Indigo'});
    this.themes.set('mdc-dark-deeppurple', {name: 'mdc-dark-deeppurple', displayName: 'MDC Dark Deeppurple'});
    this.themes.set('mdc-dark-indigo', {name: 'mdc-dark-indigo', displayName: 'MDC Dark Indigo'});
    this.themes.set('mdc-light-deeppurple', {name: 'mdc-light-deeppurple', displayName: 'MDC Light Deeppurple'});
    this.themes.set('mdc-light-indigo', {name: 'mdc-light-indigo', displayName: 'MDC Light Indigo'});
    this.themes.set('mira', {name: 'mira', displayName: 'Mira'});
    this.themes.set('nano', {name: 'nano', displayName: 'Nano'});
    this.themes.set('nova-accent', {name: 'nova-accent', displayName: 'Nova Accent'});
    this.themes.set('nova-alt', {name: 'nova-alt', displayName: 'Nova Alt'});
    this.themes.set('nova', {name: 'nova', displayName: 'Nova'});
    this.themes.set('rhea', {name: 'rhea', displayName: 'Rhea'});
    this.themes.set('saga-blue', {name: 'saga-blue', displayName: 'Saga Blue'});
    this.themes.set('saga-green', {name: 'saga-green', displayName: 'Saga Green'});
    this.themes.set('saga-orange', {name: 'saga-orange', displayName: 'Saga Orange'});
    this.themes.set('saga-purple', {name: 'saga-purple', displayName: 'Saga Purple'});
    this.themes.set('soho-dark', {name: 'soho-dark', displayName: 'Soho Dark'});
    this.themes.set('soho-light', {name: 'soho-light', displayName: 'Soho Light'});
    this.themes.set('tailwind-light', {name: 'tailwind-light', displayName: 'Tailwind Light'});
    this.themes.set('vela-blue', {name: 'vela-blue', displayName: 'Vela Blue'});
    this.themes.set('vela-green', {name: 'vela-green', displayName: 'Vela Green'});
    this.themes.set('vela-orange', {name: 'vela-orange', displayName: 'Vela Orange'});
    this.themes.set('vela-purple', {name: 'vela-purple', displayName: 'Vela Purple'});
    this.themes.set('viva-dark', {name: 'viva-dark', displayName: 'Viva Dark'});
    this.themes.set('viva-light', {name: 'viva-light', displayName: 'Viva Light'});

    this.currentTheme = this.themes.get(this.defaultTheme)!;
  }

  getThemes(): IterableIterator<Theme> {
    return this.themes.values();
  }

  getCurrentTheme(): Theme {
    return this.currentTheme;
  }

  applyThemeFromStorage() {
    const currentTheme = localStorage.getItem(this.themeStorageKey);
    this.applyTheme(currentTheme ?? this.defaultTheme);
  }

  switchTheme(theme: string) {
    this.applyTheme(theme);
  }

  private applyTheme(theme: string) {
    const themeObj = this.themes.get(theme);

    if (!themeObj) {
      return;
    }

    const themeLink = this.document.getElementById('app-theme') as HTMLLinkElement;

    if (themeLink) {
      themeLink.href = `theme-${theme}.css`;
      localStorage.setItem(this.themeStorageKey, theme);
      this.currentTheme = themeObj;
    }
  }
}

export interface Theme {
  name: string;
  displayName: string;
}
