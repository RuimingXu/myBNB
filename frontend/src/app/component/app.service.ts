import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {Rental} from "../model/app.rent";

const server = 'http://localhost:8090';
const searchUrl = '/control/search';
const initUrl = '/control';
const setUrl = '/control/initData';

@Injectable()
export class ConfigService {

  baseUrl = server;
  config = initUrl;
  info = searchUrl;
  init = setUrl;

  constructor(private http: HttpClient) { }

  getConfig() {
    return this.http.get(this.baseUrl + this.config);
  }

  errorHandler(error:HttpErrorResponse) {
    return Observable.throw(error.message || "Server Error")
  }

  getInfo (url: string, limit:string) {
    let para = new HttpParams().set('seend', url).set('size',limit);
    return this.http.post<Rental[]>(this.baseUrl + this.info, {}, {params: para});
  }

  getInit() {
    return this.http.get<Rental[]>(this.baseUrl + this.init);
  }
}
