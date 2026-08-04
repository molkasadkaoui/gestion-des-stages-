import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  currentLang = 'fr';

  constructor(private translate: TranslateService) {
    const saved = localStorage.getItem('lang') || 'fr';
    this.setLanguage(saved);
  }

  setLanguage(lang: string): void {
    this.currentLang = lang;
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
    document.documentElement.setAttribute('lang', lang);
    document.documentElement.setAttribute('dir', lang === 'ar' ? 'rtl' : 'ltr');
  }

  toggle(): void {
    this.setLanguage(this.currentLang === 'fr' ? 'ar' : 'fr');
  }
}
