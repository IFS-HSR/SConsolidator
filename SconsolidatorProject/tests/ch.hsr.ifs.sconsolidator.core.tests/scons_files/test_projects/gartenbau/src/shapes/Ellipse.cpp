#include "Ellipse.h"
#include <cmath>
#include <stdexcept>

/*
 * Assuming that a and b are the semi-axes.
 */
Ellipse::Ellipse(double a, double b): a(a), b(b) {
	if((a <= 0) || (b <= 0))
		throw std::out_of_range("Error: positive parameter expected!");
}

unsigned int Ellipse::pegs() const{
	return 3;
}

double Ellipse::ropes() const{
	double squareLam = pow(((a-b)/(a+b)), 2);
	return Shape::PI*(a+b)*(1+((3*squareLam)/(10+pow(4-3*squareLam, 0.5))))+(2*std::max(a, b));
}

double Ellipse::seeds() const{
	return ((a*b)*Shape::PI)*seedRatio;
}

std::string Ellipse::name() const{
	return "Ellipse\t";
}
