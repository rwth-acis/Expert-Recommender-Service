package com.sathvik.ers;

import java.util.HashMap;
import java.util.Set;

import org.ejml.simple.SimpleMatrix;

import com.sathvik.models.Resource;
import com.sathvik.utils.Utils;

/*
 *	@author - Sathvik.
 *  Associate index of matrix with Ids of the object.
 *  
 *   TODO://Extend it to accept ids in String.
 *   
 * */


public class SimpleIndexedMatrix extends SimpleMatrix {
	double[] indexArray;

	public SimpleIndexedMatrix(Set obj) {
		super(obj.size(), obj.size());
		
		indexArray = new double[obj.size()];

		int i = 0;
		for (Object key : obj) {
			indexArray[i] = (Integer) key;
			i++;
		}

	}
	
	public double[] getIndexArray() {
		return indexArray;
	}

	public double getValue(int rowid, int colid) {
		int rowIndex = find(rowid);
		int colIndex = find(colid);

		return super.mat.get(rowIndex, colIndex);
	}

	public void setValue(double rowId, double colId, double value) {
		int rowIndex = find(rowId);
		int colIndex = find(colId);
		
		super.mat.set(rowIndex, colIndex, value);
	}

	public int find(double value) {
		for (int i = 0; i < indexArray.length; i++)
			if (indexArray[i] == value)
				return i;
		return -1;
	}
	
	public void printRow(int rowNo) {
		for(int i = 0 ; i < super.mat.numCols ; i++) {
			if(super.mat.get(rowNo, i) == 1) {
				Utils.print(super.mat.get(rowNo, i)+", ");
			}
		}
	}

}
