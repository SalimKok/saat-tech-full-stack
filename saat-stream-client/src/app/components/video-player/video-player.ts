import { Component, ElementRef, Input, OnDestroy, AfterViewInit, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import videojs from 'video.js';
import 'videojs-youtube';

@Component({
  selector: 'app-video-player',
  standalone: true,
  template: `
    <div class="video-container" style="width: 100%; border-radius: 8px; overflow: hidden; background: #000; box-shadow: 0 10px 30px rgba(0,0,0,0.8);">
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
        const isYoutube = this.isYoutubeUrl(this.src);
        const videoType = isYoutube ? 'video/youtube' : this.type;
        
        this.player.src({ src: this.src, type: videoType });
        this.player.load();
        this.player.play(); 
      }
    }
  }

  initPlayer() {
    if (this.target && this.target.nativeElement) {
      const isYoutube = this.isYoutubeUrl(this.src);
      const videoType = isYoutube ? 'video/youtube' : this.type;
      
      const playerOptions: any = {
        controls: true,
        autoplay: true, 
        preload: 'auto',
        fluid: true, 
        sources: [{
          src: this.src,
          type: videoType
        }]
      };

      if (isYoutube) {
        playerOptions.techOrder = ['youtube'];
      }

      this.player = videojs(this.target.nativeElement, playerOptions);
    }
  }

  private isYoutubeUrl(url: string): boolean {
    if (!url) return false;
    return url.includes('youtube.com') || url.includes('youtu.be');
  }

  ngOnDestroy() {
    if (this.player) {
      this.player.dispose();
    }
  }
}
