import { Routes } from '@angular/router';
import { ContentSearchComponent } from './components/content-search/content-search';
import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { authGuard } from './core/guards/auth-guard';
import { ContentDetailComponent } from './components/content-detail/content-detail';

export const routes: Routes = [
  { path: 'auth-callback', component: AuthCallbackComponent },

  { path: 'search', component: ContentSearchComponent, canActivate: [authGuard] },
  
  { path: 'watch/:id', component: ContentDetailComponent, canActivate: [authGuard] },

  { path: '', redirectTo: 'search', pathMatch: 'full' }
];
