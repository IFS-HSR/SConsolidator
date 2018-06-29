#include "Diamond.h"
#include <cmath>
#include <stdexcept>

Diamond::Diamond(double a, double b, double angle): a(a), b(b), angle(angle) {
	if((a <= 0) || (b <= 0) || (angle <= 0))
		throw std::out_of_range("Error: positive parameter expected!");
	else if(angle > 179)
		throw std::out_of_range("Error: angle has to be less than 180!");
}


unsigned int Diamond::pegs() const{
	return 4;
}

double Diamond::ropes() const{
	return 2*(a+b);
}

double Diamond::seeds() const{
	return ((a*b*sin(angle*M_PI/180))*seedRatio);
}

std::string Diamond::name() const{
	return "Diamond\t";
}
