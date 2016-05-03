import {Component, NgZone, Input} from "angular2/core";
import {CalculateService} from "app/graphs/services/calculate.service";

var zingchart: Zingchart;

class Chart
{
    id: String;
    data;
    height: any;
    width: any;
    constructor(config) 
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
class ZingChart
{
    chart: Chart;
    constructor(private zone: NgZone)
    { }

    ngAfterViewInit()
    {
        this.zone.runOutsideAngular(() =>
        {
            zingchart.render(
                {
                    id: this.chart['id'],
                    data: this.chart['data'],
                    width: this.chart['width'],
                    height: this.chart['height'],
                    events:
                    {
                        load: function()
                        {
                            $("#chart-corr").drawTrendline();
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
		providers: [CalculateService],
        templateUrl: "corr_view.html"
    })
export class CorrView 
{
    @Input("chart-id") id: string;

    charts: Chart[];

    constructor(private _calculateService: CalculateService) { }


    update(data)
    {
        var info = this._calculateService.calculate(Object.keys(data.data).map(k=> [data.data[k].a, data.data[k].b]));

        this.charts =
            [{

                id: "chart-" + this.id,
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

                    },

                    plotarea:
                    {
                        margin: "75px"
                    },
                    "tooltip":
                    {
                        "html-mode": true,
                        "border-width": 2,
                        "border-radius": 5,
                        "border-color": "#000",
                        "text": "<table><tr><td>Datum:</td><td>%data-dates</td></tr><tr><td>%data-name-a:</td><td>%v %data-unit-a</td></tr><tr><td>%data-name-b:</td><td>%k %data-unit-b</td></tr></table>"
                    },
                    series:
                    [{
                        values: Object.keys(data.data).map(k => [data.data[k].a, data.data[k].b]),
                        "data-dates": Object.keys(data.data),
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

    ngOnInit()
    {
        this.clear();
    }

    clear()
    {
        this.charts =
            [{

                id: "chart-" + this.id,
                data:
                {
                    "labels":
                    [{
                        "text": "No values found",
                        "font-family": "Georgia",
                        "font-size": "50",
                        "font-color": "red",
                        "x": "20%",
                        "y": "40%"
                    }]
                },
                height: 400,
                width: 600
            }];
    }
}