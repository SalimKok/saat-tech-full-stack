import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ConfirmService, ConfirmState } from '../../services/confirm-service'; 

@Component({
  selector: 'app-confirm-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-modal.html',
  styleUrl: './confirm-modal.css'
})
export class ConfirmModalComponent implements OnInit, OnDestroy {
  state: ConfirmState | null = null;
  private subscription!: Subscription;

  constructor(private confirmService: ConfirmService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.subscription = this.confirmService.confirmState$.subscribe((state) => {
      this.state = state;
      this.cdr.detectChanges();
    });
  }

  onConfirm() {
    if (this.state) {
      this.state.resolve(true); 
      this.confirmService.close();
    }
  }

  onCancel() {
    if (this.state) {
      this.state.resolve(false); 
      this.confirmService.close();
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
