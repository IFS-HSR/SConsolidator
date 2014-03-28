#ifndef RECTANGLE_H_
#define RECTANGLE_H_

#include "Shape.h"

class Rectangle: public Shape {
public:
	Rectangle(double a, double b);
	unsigned int pegs() const;
	double ropes() const;
	double seeds() const;
	std::string name() const;
private:
	double a, b;
};

#endif /* RECTANGLE_H_ */
