import { Routes } from '@angular/router';
import { ContentSearchComponent } from './components/content-search/content-search';
import { AuthCallbackComponent } from './core/auth-callback/auth-callback';
import { ContentDetailComponent } from './components/content-detail/content-detail';

export const routes: Routes = [
  { path: 'auth-callback', component: AuthCallbackComponent },

  { path: 'search', component: ContentSearchComponent},
  
  { path: 'watch/:id', component: ContentDetailComponent},

  { path: '', redirectTo: 'search', pathMatch: 'full' }
];
