#include "Rectangle.h"
#include <stdexcept>

Rectangle::Rectangle(double a, double b): a(a), b(b) {
	if((a <= 0) || (b <= 0))
		throw std::out_of_range("parameter have to be greater then zero...");
}

unsigned int Rectangle::pegs() const{
	return 4;
}

double Rectangle::ropes() const{
	return 2*(a*b);
}

double Rectangle::seeds() const{
	return (a*b)*seedRatio;
}

std::string Rectangle::name() const{
	return "Rectangle";
}
