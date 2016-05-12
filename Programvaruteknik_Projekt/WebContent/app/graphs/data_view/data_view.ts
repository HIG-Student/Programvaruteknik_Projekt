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
	templateUrl: "app/graphs/data_view/data_view.html",
	styleUrls: ["app/graphs/data_view/data_view.css"]
})
export class DataView 
{
	private _data:object;
	
	missing_data:int = 0;
	
	@Input()
	set data(data:object)
	{
		console.log("GNUUUUUU",data);
		
		this._data = data;
		if(data == null)
		{
			this.clear();
		}
		else
		{
			var keys =  Object.keys(data.data);
			
			/*
			keys.sort( (a, b) =>
			{
				return a - b;
			});
			*/
			
			var values = keys.map(k=>[k,Date.parse(k),data.data[k]);

			var first = values[0][1];
			var last = values[values.length-1][1];
			var step = 1000 * 60 * 60 * 24;
			var steps = (last - first) / step;
			
			this.missing_data = steps - values.length;
			
			var new_data = {};
			
			for(i = first;i < last;i += step)
				new_data["D:"+i.toString()] = null;
			
			values.forEach(value=>
			{
				new_data["D:"+value[1].toString()] = value;
			});
				
			values = Object.keys(new_data).map(k=>
			{
				if(new_data[k] == null)
					return null;
				else
					return [new_data[k][1],new_data[k][2]];
			});
			
			if(values.length == 0)
			{
				this.clear();
			}
			else
			{
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
							"min-value": first,
							"step":"day",
							"transform":
							{
								"type":"date",
								"all":"%Y-%mm-%dd"
							}
						},
						series: 
						[{
							values: values
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
	
	clear()
	{
		this.missing_data = 0;
		
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
	id:int = "chart-source-" + DataView.elements++;
	
	ngOnInit()
	{
		this.clear();
	}
}