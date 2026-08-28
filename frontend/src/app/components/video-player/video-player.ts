import { Component, ElementRef, Input, OnDestroy, AfterViewInit, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import videojs from 'video.js';

@Component({
  selector: 'app-video-player',
  standalone: true,
  template: `
    <div class="video-container" style="width: 100%; border-radius: 8px; overflow: hidden; background: #000; box-shadow: 0 4px 15px rgba(0,0,0,0.5);">
      <video #target class="video-js vjs-default-skin vjs-big-play-centered" controls playsinline preload="auto"></video>
    </div>
  `
})
export class VideoPlayerComponent implements AfterViewInit, OnDestroy, OnChanges {
  @ViewChild('target', { static: true }) target!: ElementRef;

  @Input() src!: string;
  @Input() type: string = 'video/mp4';

  player: any;

  ngAfterViewInit() {
    this.initPlayer();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['src'] && !changes['src'].isFirstChange()) {
      if (this.player) {
        this.player.src({ src: this.src, type: this.type });
        this.player.load();
      }
    }
  }

  initPlayer() {
    if (this.target && this.target.nativeElement) {
      this.player = videojs(this.target.nativeElement, {
        controls: true,
        autoplay: false,
        preload: 'auto',
        fluid: true, 
        sources: [{
          src: this.src,
          type: this.type
        }]
      });
    }
  }

  ngOnDestroy() {
    if (this.player) {
      this.player.dispose();
    }
  }
}
