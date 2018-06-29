#include "Shape.h"
#include <iostream>
#include <cmath>

Shape::~Shape() {}

const double Shape::PI = 4.0 * atan(1.0);

std::ostream& operator<<(std::ostream& os, Shape const& sh){
	return os << sh.name() << "\t" << sh.pegs() << "\t" << sh.ropes() << "\t" << sh.seeds();
}
