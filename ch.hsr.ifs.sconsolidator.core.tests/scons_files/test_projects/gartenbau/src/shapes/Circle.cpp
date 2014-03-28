#include "Circle.h"
#include <cmath>
#include <stdexcept>


Circle::Circle(double r): r(r){
	if(r <= 0)
		throw std::out_of_range("Error: positive parameter expected!");
}


unsigned int Circle::pegs() const{
	return 2;
}

double Circle::ropes() const{
	return ((2*r)*Shape::PI)+r;
}

double Circle::seeds() const{
	return ((r*r)*Shape::PI)*seedRatio;
}

std::string Circle::name() const{
	return "Circle\t";
}
