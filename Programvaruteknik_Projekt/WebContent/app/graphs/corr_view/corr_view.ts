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
				events:
				{
					load:function()
					{
						if(this.chart)
            				$(this.chart['id']).drawTrendline();
        			}
    			}
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
	selector: "corr-view",
	directives: [ZingChart],
	templateUrl: "app/graphs/corr_view/corr_view.html",
	styleUrls: ["app/graphs/corr_view/corr_view.css"]
})
export class CorrView 
{
	charts: Chart[];
	
	constructor() { }
	
	calculate(xyValues)
	{
		var xValues = [xyValues.length];
		var yValues = [xyValues.length];
	
		for(var i=0; i<xyValues.length;i++)
		{
			xValues[i]=xyValues[i][0];
			yValues[i]=xyValues[i][1];
		}	

		var nbrOfElements = xyValues.length;
		var xSum = getSumOf(xValues);
		var ySum = getSumOf(yValues);
		var xySum = getSumOfArrayNodeProducts(xValues, yValues);
		var xSquaredSum = getSumOfArrayNodeProducts(xValues, xValues);
		var ySquaredSum = getSumOfArrayNodeProducts(yValues, yValues);
	
		function getSumOfArrayNodeProducts(aValues,bValues)
		{
			var sum = 0;
			for (var i = 0; i < nbrOfElements; i++) 
			{
				sum += aValues[i] * bValues[i];
			}
			return sum;
		}
		
		function getSumOf(values) 
		{
			var sum = 0;
			for (var i = 0; i < values.length; i++) 
			{
				sum += values[i];
			}
			return sum;
		}
		
		function getKValue()
		{
			return ((nbrOfElements*xySum)-(xSum*ySum))/((nbrOfElements*xSquaredSum)-(xSum*xSum));
		}
		
		function getMValue()
		{
			return ((xSquaredSum*ySum)-(xSum*xySum))/
				((nbrOfElements*xSquaredSum)-(xSum*xSum));
		}
		
		function getRValue() 
		{
			return (Math.pow(((nbrOfElements*xySum)-(xSum*ySum)), 2))/
				(((nbrOfElements*xSquaredSum)-(xSum*xSum))*((nbrOfElements*ySquaredSum)-(ySum*ySum)));
		}
		
		function getRegLineMinYValue() 
		{
			return straightLineEqFofX(getMinX());
		}

		function getRegLineMaxYValue() 
		{
			return straightLineEqFofX(getMaxXValue());
		}
		
		function straightLineEq( x )
		{
			return (getKValue()*x)+getMValue();
		}

		function getMinX() 
		{
			var minX = null;
			for (var i = 0; i < xValues.length; i++) 
			{
				if (minX == null || minX > xValues[i])
					minX = xValues[i];
			}
			return minX;
		}

		function getMaxX()
		{
			var maxX = null;
			for (var i = 0; i < xValues.length; i++) 
			{
				if (maxX == null || maxX < xValues[i])
					maxX = xValues[i];
			}
			return maxX;
		}
		
		return {
			"m": getMValue(),
			"k": getKValue(),
			"R": getRValue(),
			"Y1": straightLineEq(getMinX()),
			"Y2": straightLineEq(getMaxX()),
		}
	}
	
	private _data:object;
	
	@Input() from:string;
	@Input() to:string;
	
	@Input()
	set data(data:object)
	{
		this._data = data;
		this.clear();
		
		if(data != null)
		{
			var from_date = Date.parse(this.from);
			var to_date = Date.parse(this.to);
				
			var values = [];
			var dates = [];
			for(var key in data.data)
			{
				var key_date = Date.parse(key);
				if(from_date != NaN && from_date > key_date)
					continue;
				if(to_date != NaN && to_date < key_date)
					continue;
					
				values.push([data.data[key].a,data.data[key].b]);
				dates.push(key);
			}
			
			var info = this.calculate(values);
			
			window.info = info;
			
			this.charts = 
			[{
			
				id: this.id,
				data:
				{	
				
					type: "scatter",
					"scale-x":
					{  
						label:
						{  
							text: data.a_name + " (" + data.a_unit + ")"
						}
					},
					"scale-y":
					{  
						label:
						{  
							text: data.b_name + " (" + data.b_unit + ")"
						},
						"markers": 
						[
				            {
				            
				                "type": "line",
				                "range": [info.Y1, info.Y2],
				                "line-style": "dashed"
				                
				            }
				        ]
				        
					},
					
					plotarea:
					{
						margin: "75px"
					},
					"tooltip":
					{
						"html-mode": true,
						"border-width":2,
						"border-radius":5,
						"border-color":"#000",
						"text": "<table><tr><td>Datum:</td><td>%data-dates</td></tr><tr><td>%data-name-a:</td><td>%v %data-unit-a</td></tr><tr><td>%data-name-b:</td><td>%k %data-unit-b</td></tr></table>" 
					},
					series: 
					[{
						values: values,
						"data-dates": dates,
						"data-name-a": data.a_name,
						"data-name-b": data.b_name,
						"data-unit-a": data.a_unit,
						"data-unit-b": data.b_unit
					}],
				},
				height: 400,
				width: 600
			}];
		}
	}
	
	get data()
	{
		return this._data;
	}
	
	static elements:int = 0;
	id:int = "chart-corr-" + CorrView.elements++;
	
	ngOnInit()
	{
		this.clear();
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
					"text": "Awaiting data",
					"font-family": "Georgia",
					"font-size": "50",
					"font-color":"green",
					"x": "20%",
					"y": "40%"
				}]
			},
			height: 400,
			width: 600
		}];
	}
}