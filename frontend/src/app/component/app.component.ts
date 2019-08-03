import {Component, OnInit} from '@angular/core';
import {ConfigService} from "./app.service";
import formValue from '../model/formValue';
declare var Chart:any;
declare var L : any;

// todo make it const from the env

@Component({
  selector: 'app-root',
  templateUrl: '../app.component.html',
  styleUrls: ['../app.component.css']
})
export class AppComponent implements  OnInit {
  public display = false;
  public dragging = false;
  public rentals = [];
  public error;
  private max: number;
  private map;
  private key;
  private chart;
  public model = new formValue('Plz input', '10');

  constructor(private service: ConfigService) {
  }

  handleClick(event: Event) {
    this.dragging = !this.dragging;
  }

  submit() {
    this.display = true;
    this.service.getInfo(this.model.url, this.model.limit).subscribe(
      data => {
        this.helper(data);
      }
    )
  }



  showConfig() {
    this.service.getConfig()
      .subscribe(data => {
          L.mapquest.key = this.key;
          this.key = data['key'];
          console.log(this.key);
          this.display = true;
          this.service.getInit().subscribe(
            tem => {
              console.log(tem);
              this.helper(tem);
            }
          )
        },
        error => this.error = error);
  }

  putOnMap() {
    L.mapquest.key = this.key;
    if (this.map != undefined) {
      this.map.off();
      this.map.remove();
    }
    if (this.rentals.length == 0) {
      this.map = L.mapquest.map('map', {
        center: [43.651893, -79.381713],
        layers: L.mapquest.tileLayer('map'),
        zoom: 10
      });
    } else {
      this.max = 0;
      for (let i = 0; i < this.rentals.length; i++) {
        let location = this.rentals[i].address;
        let postDate = this.rentals[i].postDate;
        let rent = this.rentals[i].rent;
        let latitude = Number(this.rentals[i].latitude);
        let longitude = Number(this.rentals[i].longitude);
        let num = parseFloat(rent.substr(1).replace(',',''));
        if( num> this.max) {
          this.max = num;
        }
        if (i == 0) {
          this.map = L.mapquest.map('map', {
            center: [latitude, longitude],
            layers: L.mapquest.tileLayer('map'),
            zoom: 10
          })
        }
        // Create a marker for each location
        L.marker([latitude, longitude], {icon: L.mapquest.icons.marker()})
          .bindPopup('<strong> Address: </strong>' + location + ' <br>' +
            "<strong> Rent: </strong>" + rent + ' <br>' +
            "<strong> PostDate: </strong>" + postDate + '<br>' +
            '<strong> Detail are showing in the Chart </strong>'
          ).addTo(this.map);
      }
      this.drawChart();
    }
  }

  drawChart() {
    console.log(this.max);
    if (this.chart != null) {
      this.chart.data.labels.pop();
      this.chart.data.datasets.forEach((dataset) => {
        dataset.data.pop();
      });
      this.chart.update();
    }

    let sepreator = Math.round(this.max /5) + 1;
    let b1 = 0;
    let u1 = sepreator;
    let b2 = u1 + 1;
    let u2 = 2*sepreator;
    let b3 = u2 + 1;
    let u3 = 3*sepreator;
    let b4 = u3 + 1;
    let u4 = 4*sepreator;
    let b5 = u4 + 1;
    let u5 = 5*sepreator;
    let count1 = 0;
    let count2 = 0;
    let count3 = 0;
    let count4 = 0;
    let count5 = 0;
    for (let i = 0; i < this.rentals.length; i++) {
      let x = parseFloat(this.rentals[i].rent.substr(1).replace(',',''));
      if (x >= b1 && x <= u1) {
        count1 = count1 + 1;
      }
      if (x >= b2 && x <= u2) {
        count2 = count2 + 1;
      }
      if (x >= b3 && x <= u3) {
        count3 = count3 + 1;
      }
      if (x >= b4 && x <= u4) {
        count4 = count4 + 1;
      }
      if (x >= b5 && x <= u5) {
        count5 = count5 + 1;
      }
    }

    let ctx = document.getElementById('myChart');
    let value = {
      datasets: [{
        data: [count1,count2 , count3,count4,count5]
      }],
      // These labels appear in the legend and in the tooltips when hovering different arcs
      labels: [
        String(b1) +  ' to ' + String(u1),
        String(b2) +  ' to ' + String(u2),
        String(b3) +  ' to ' + String(u3),
        String(b4) +  ' to ' + String(u4),
        String(b5) +  ' to ' + String(u5)
      ],
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(255, 159, 64, 0.2)'
      ],
      borderColor: [
        'rgba(255,99,132,1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)'
      ]
    };
    this.chart = new Chart(ctx, {
      type: 'doughnut',
      data: value,
      options: {}
    });
  }


  helper(data) {
    this.rentals = data;
    console.log(this.rentals);
    this.putOnMap();
    this.display = false;
  }
  ngOnInit(): void {
    // this can be use to get api key from backend
    this.showConfig();
    //this.getGeo()
  }
}





