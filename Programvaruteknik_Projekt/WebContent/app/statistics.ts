import {Component,OnInit,View} from "angular2/core";
import {RouteConfig,Route, Router, ROUTER_DIRECTIVES} from 'angular2/router';
import {GraphView} from "app/graphs/graph_view/graph_view";
import {LoginView} from "app/login_view/login_view";
import {DataLoader} from "app/data_loader";
import {ProgressService} from "app/progress_service";
import {HTTP_PROVIDERS} from 'angular2/http';

console.log("R",Route);

@View((
{
	templateUrl: "app/statistics.html",
	styleUrls: ["app/statistics.css"],
	directives: [GraphView,LoginView,ROUTER_DIRECTIVES],
	providers: [ProgressService,DataLoader,HTTP_PROVIDERS]
})
@RouteConfig(
[
	{
		path: '/login',  
		component: LoginView,
		useAsDefault: true
	},
	{
		path: '/statistics',  
		component: GraphView
	}
])
export class Statistics implements OnInit
{
	constructor(private router: Router, private progressor: ProgressService) 
	{
		
	}
}