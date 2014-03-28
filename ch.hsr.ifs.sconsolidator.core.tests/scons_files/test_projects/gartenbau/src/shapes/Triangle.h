#ifndef TRIANGLE_H_
#define TRIANGLE_H_

#include "Shape.h"

class Triangle: public Shape {
public:
	Triangle(double a, double b, double c);
	unsigned int pegs() const;
	double ropes() const;
	double seeds() const;
	std::string name() const;
private:
	double a, b, c;

	std::string checkTriangle();
};

#endif /* TRIANGLE_H_ */
