import { Routes } from '@angular/router';
import { ContentSearch } from './components/content-search/content-search';
import { ContentList } from './components/content-list/content-list';
import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { CastList } from './components/cast-list/cast-list';
import { CastForm } from './components/cast-form/cast-form';
import { ContentManagerDashboard } from './components/content-manager-dashboard/content-manager-dashboard';

export const routes: Routes = [
  { path: 'auth-callback', component: AuthCallbackComponent },

  { path: 'movies', component: ContentList},
  { path: 'search', component: ContentSearch},
  { path: 'casts', component: CastList},
  { path: 'casts/add', component: CastForm},
  { path: 'casts/edit/:id', component: CastForm},
   { path: 'content/:id', component: ContentManagerDashboard},

  { path: '', redirectTo: 'movies', pathMatch: 'full' }
];
