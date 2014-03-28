#ifndef ELLIPSE_H_
#define ELLIPSE_H_

#include "Shape.h"

class Ellipse: public Shape {
public:
	Ellipse(double a, double b);
	unsigned int pegs() const;
	double ropes() const;
	double seeds() const;
	std::string name() const;
private:
	double a, b;
};

#endif /* ELLIPSE_H_ */
