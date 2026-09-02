import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface ConfirmState {
  title: string;
  message: string;
  resolve: (value: boolean) => void;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmService {
  private confirmSubject = new Subject<ConfirmState | null>();
  confirmState$ = this.confirmSubject.asObservable();

  ask(title: string, message: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.confirmSubject.next({
        title,
        message,
        resolve
      });
    });
  }

  close() {
    this.confirmSubject.next(null);
  }
}
