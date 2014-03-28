#ifndef CIRCLE_H_
#define CIRCLE_H_

#include "Shape.h"
#include <string>

class Circle: public Shape {
public:
	explicit Circle(double r);
	unsigned int pegs() const;
	double ropes() const;
	double seeds() const;
	std::string name() const;
private:
	double r;
};

#endif /* CIRCLE_H_ */
