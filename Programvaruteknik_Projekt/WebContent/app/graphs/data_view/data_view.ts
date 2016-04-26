import {Component, NgZone, AfterView, OnDestroy, Input} from "angular2/core";

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
	constructor(private zone: NgZone) { }

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
	private _data:object;
	
	@Input()
	set data(data:object)
	{
		this._data = data;
		if(data == null)
		{
			this.clear();
		}
		else
		{
			var values = Object.keys(data.data).map(k=>[k,Date.parse(k),data.data[k]);
			
			window.values1 = values;
			
			if(values.length == 0)
			{
				this.clear();
			}
			else
			{
				var first = values[0];

				this.charts = 
				[{
					id: this.id,
					data:
					{
						type: "line",
						plot:
						{ 
							aspect: "spline" 
						},
						"scale-x":
						{
							"min-value": first[1],
							"step":"day",
							"transform":
							{
								"type":"date",
								"all":"%Y-%mm-%dd"
							}
						},
						series: 
						[{
							values: Object.keys(values).map(k=>[values[k][1],values[k][2]])
						}],
					},
					height: 400,
					width: 600
				}];
			}
		}
	}
	
	get data()
	{
		return this._data;
	}

	charts: Chart[];
	
	constructor() { }
	
	setData(o:object)
	{
	
	}
	
	clear()
	{
		this.charts = 
		[{
			id: this.id,
			data:
			{
				"labels":
				[{
					"text": "No values found",
					"font-family": "Georgia",
					"font-size": "50",
					"font-color":"red",
					"x": "20%",
					"y": "40%"
				}]
			},
			height: 400,
			width: 600
		}];
	}
	
	static elements:int = 0;
	id:int = "chart-" + DataView.elements++;
	
	ngOnInit()
	{
		this.clear();
	}
}