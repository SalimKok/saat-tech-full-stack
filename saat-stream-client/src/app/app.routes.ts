import { Routes } from '@angular/router';
import { ContentSearchComponent } from './components/content-search/content-search';
import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  // Bilet kontrol sayfası (Bekçi buraya karışmaz)
  { path: 'auth-callback', component: AuthCallbackComponent },

  // Yayın platformu sayfaları (authGuard bekçisi koruyor)
  { path: 'search', component: ContentSearchComponent, canActivate: [authGuard] },

  // Giren herkesi otomatik olarak arama ekranına (dolayısıyla bekçiye) at
  { path: '', redirectTo: 'search', pathMatch: 'full' }
];
