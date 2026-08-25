import { Routes } from '@angular/router';
import { ContentSearch } from './components/content-search/content-search';
import { ContentList } from './components/content-list/content-list';
import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { authGuard } from './core/guards/auth-guard';
import { CastList } from './components/cast-list/cast-list';
import { CastForm } from './components/cast-form/cast-form';

export const routes: Routes = [
  { path: 'auth-callback', component: AuthCallbackComponent },

  { path: 'movies', component: ContentList, canActivate: [authGuard] },
  { path: 'search', component: ContentSearch, canActivate: [authGuard] },
  { path: 'casts', component: CastList, canActivate: [authGuard] },
  { path: 'casts/add', component: CastForm, canActivate: [authGuard] },
  { path: 'casts/edit/:id', component: CastForm, canActivate: [authGuard] },

  { path: '', redirectTo: 'movies', pathMatch: 'full' }
];
