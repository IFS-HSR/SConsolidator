#include "Triangle.h"
#include <cmath>
#include <stdexcept>
#include <string>
#include <sstream>

Triangle::Triangle(double a, double b, double c) :
	a(a), b(b), c(c) {
	if ((a <= 0) || (b <= 0) || (c <= 0))
		throw std::out_of_range("Error: positive parameter expected!");
	else if (!(a + b > c && a + c > b && b + c > a)){
		std::stringstream ss;
		ss << "Error: no triangle exists with sides " << a << ", " << b << ", " << c << " because " << checkTriangle();
		throw std::out_of_range(ss.str());
	}
}

std::string Triangle::checkTriangle(){
	using namespace std;

	double minV, middleV, maxV;

	minV = min(a, min(b, c));
	maxV = max(a, max(b, c));
	middleV = (a + b + c) - maxV - minV;

	stringstream result;
	result <<  minV << " + " << middleV << " <= " << maxV;
	return result.str();
}

unsigned int Triangle::pegs() const {
	return 3;
}

double Triangle::ropes() const {
	return (a + b + c);
}

double Triangle::seeds() const {
	double s = (a + b + c) / 2;
	return pow(s * (s - a) * (s - b) * (s - c), 0.5) * seedRatio;
}

std::string Triangle::name() const {
	return "Triangle";
}
