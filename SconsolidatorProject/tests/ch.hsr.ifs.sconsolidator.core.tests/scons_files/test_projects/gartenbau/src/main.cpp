#include "Circle.h"
#include "Diamond.h"
#include "Ellipse.h"
#include "Rectangle.h"
#include "Square.h"
#include "Triangle.h"

#include <iostream>
#include <vector>
#include <iterator>
#include <numeric>
#include <tr1/memory>
#include <tr1/functional>

using namespace std;

typedef tr1::shared_ptr<Shape> ShapePtr;
typedef vector<ShapePtr> GardenPlan;

struct addPegs:std::binary_function<unsigned int, ShapePtr, unsigned int>{
	unsigned int operator()(unsigned int val, ShapePtr shape) const{
		return val+shape->pegs();
	}
};

unsigned int calculatePegs(GardenPlan const& p){
	return std::accumulate(p.begin(), p.end(), 0u, addPegs());
}

struct addSeeds:std::binary_function<double, ShapePtr, double>{
	double operator()(double val, ShapePtr shape) const{
		return val+shape->seeds();
	}
};

struct addRope:std::binary_function<double,ShapePtr, double>{
	double operator()(double val,ShapePtr shape)const{
		return val + shape->ropes();
	}
};

double sum(GardenPlan vec, tr1::function<double (double, ShapePtr)> functor){
	return accumulate(vec.begin(), vec.end(), 0.0, functor);
}

void printList(GardenPlan vec, ostream& os){
	copy(vec.begin(), vec.end(), ostream_iterator<ShapePtr>(os, "\n"));
}

int main(){
	GardenPlan plan;
	plan.push_back(ShapePtr(new Triangle(3,6, 6.70820393)));
	plan.push_back(ShapePtr(new Triangle(3,4,4)));
	plan.push_back(ShapePtr(new Triangle(3,4,4)));
	plan.push_back(ShapePtr(new Diamond(4,4,60)));
	plan.push_back(ShapePtr(new Square(3)));
	plan.push_back(ShapePtr(new Square(3)));
	plan.push_back(ShapePtr(new Rectangle(4,9)));
	plan.push_back(ShapePtr(new Circle(4)));
	plan.push_back(ShapePtr(new Ellipse(3, 5)));

	cout << "shape\t\tpegs\trope\tseeds" << endl;
	cout << "=========================================" << endl;
	printList(plan, cout);
	cout << endl;

	cout << "pegs needed:\t" << sum(plan, addPegs()) << endl;
	cout << "seeds needed:\t"<< sum(plan, addSeeds()) <<" kg"<< endl;
	cout << "rope needed:\t" << sum(plan, addRope()) << " m"<<endl;
}
