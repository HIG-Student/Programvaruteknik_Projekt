import {Injectable} from 'angular2/core';

@Injectable()
export class ProgressService
{
	private loadings:int = 0;
	
	public get loading():boolean
	{
		return this.loadings > 0;
	}
	
	public addLoading()
	{
		this.loadings++;
	}
	
	public removeLoading()
	{
		this.loadings--;
	}
}