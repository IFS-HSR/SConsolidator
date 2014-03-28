#ifndef DIAMOND_H_
#define DIAMOND_H_

#include "Shape.h"

class Diamond: public Shape {
public:
	Diamond(double a, double b, double angle);

	unsigned int pegs() const;
	double ropes() const;
	double seeds() const;
	std::string name() const;
private:
	double a, b, angle;
};

#endif /* DIAMOND_H_ */
