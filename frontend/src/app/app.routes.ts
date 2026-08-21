import { Routes } from '@angular/router';
import { ContentSearch } from './components/content-search/content-search';
import { ContentList } from './components/content-list/content-list';
import { CastManagement } from './components/cast-management/cast-management';

import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: 'auth-callback', component: AuthCallbackComponent },

  { path: 'movies', component: ContentList, canActivate: [authGuard] },
  { path: 'search', component: ContentSearch, canActivate: [authGuard] },
  { path: 'casts', component: CastManagement, canActivate: [authGuard] },

  { path: '', redirectTo: 'movies', pathMatch: 'full' }
];
