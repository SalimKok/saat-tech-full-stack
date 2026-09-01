import { Routes } from '@angular/router';
import { ContentSearch } from './components/content-search/content-search';
import { ContentList } from './components/content-list/content-list';
import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { CastList } from './components/cast-list/cast-list';
import { CastEdit } from './components/cast-edit/cast-edit';
import { CastDetail } from './components/cast-detail/cast-detail';
import { ContentManagerDashboard } from './components/content-manager-dashboard/content-manager-dashboard';

export const routes: Routes = [
  { path: 'auth-callback', component: AuthCallbackComponent },

  { path: 'contents', component: ContentList},
  { path: 'create-content', component: ContentSearch},
  { path: 'casts', component: CastList},
  { path: 'casts/add', component: CastEdit },
  { path: 'casts/edit/:id', component: CastEdit },
  { path: 'casts/details/:id', component: CastDetail },
  { path: 'content/:id', component: ContentManagerDashboard},

  { path: '', redirectTo: 'contents', pathMatch: 'full' }
];
