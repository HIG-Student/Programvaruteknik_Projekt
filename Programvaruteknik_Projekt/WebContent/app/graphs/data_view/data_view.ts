import {Component, NgZone, AfterView, OnDestroy, Input} from "angular2/core";
import {DataLoader} from "app/data_loader";

class Chart
{ 
	id: String;
	data: Object;
	height: any;
	width: any;
	constructor(config: Object) 
	{
		this.id = config['id'];
		this.data = config['data'];
		this.height = config['height'] || 300;
		this.width = config['width'] || 600;
	}
}

@Component(
{
	selector: 'zingchart',
	inputs: ['chart'],
	template: "<div id='{{chart.id}}'></div>"
})
class ZingChart implements AfterView, OnDestroy
{
	chart: Chart;
	constructor(private zone: NgZone)
	{}

	ngAfterViewInit()
	{
		this.zone.runOutsideAngular(() =>
		{
			zingchart.render(
			{
				id: this.chart['id'],
				data: this.chart['data'],
				width: this.chart['width'],
				height: this.chart['height']
			});
		});
	}
	ngOnDestroy()
	{
		zingchart.exec(this.chart['id'], 'destroy');
	}
}



@Component(
{
	selector: "data-view",
	directives: [ZingChart],
	templateUrl: "app/graphs/data_view/data_view.html"
})
export class DataView 
{
	@Input("chart-id") id: string;

	charts: Chart[];
	
	constructor(private dataLoader: DataLoader) { }
	
	ngOnInit()
	{
		this.name = 'Angular2'
		
		this.dataLoader.getDataSource().subscribe(data =>
		{
			this.charts = 
			[{
				id: "chart-" + this.id,
				data:
				{
					type: "line",
					plot:
					{ 
						aspect: "spline" 
					}, 
					series: 
					[{
						values: Object.keys(data.data).map(k=>[data.data[k].a,data.data[k].b])
					}],
				},
				height: 400,
				width: 600
			}]
		});
	}
}