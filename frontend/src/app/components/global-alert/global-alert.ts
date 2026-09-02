import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { AlertService, Alert } from '../../services/alert-service'; 

@Component({
  selector: 'app-global-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './global-alert.html',
  styleUrl: './global-alert.css'
})
export class GlobalAlertComponent implements OnInit, OnDestroy {
  alerts: Alert[] = [];
  private subscription!: Subscription;

  constructor(private alertService: AlertService, private cdr: ChangeDetectorRef) {} 

  ngOnInit() {
    this.subscription = this.alertService.alert$.subscribe((alert) => {
      this.alerts.push(alert);

      this.cdr.detectChanges(); 
      
      setTimeout(() => {
        this.removeAlert(alert);
      }, 3500);
    });
  }

  removeAlert(alert: Alert) {
    this.alerts = this.alerts.filter(a => a !== alert);
    
    this.cdr.detectChanges(); 
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
