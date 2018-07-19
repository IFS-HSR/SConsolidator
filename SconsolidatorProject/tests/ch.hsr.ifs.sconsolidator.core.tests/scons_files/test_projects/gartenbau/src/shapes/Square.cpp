#include "Square.h"
#include <stdexcept>


Square::Square(double s): s(s) {
	if(s <= 0)
		throw std::out_of_range("Error: positive parameter expected!");
}

unsigned int Square::pegs() const{
	return 4;
}

double Square::ropes() const{
	return (4*s);
}

double Square::seeds() const{
	return (s*s)*seedRatio;
}

std::string Square::name() const{
	return "Square\t";
}
