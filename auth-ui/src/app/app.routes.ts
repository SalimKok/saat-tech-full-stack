import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login';
import { RegisterComponent } from './features/register/register';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  
  { path: 'register', component: RegisterComponent },
  
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: '**', redirectTo: 'login' }
];
