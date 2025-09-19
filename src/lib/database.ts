import { prisma } from './prisma';

// Clinic operations
export const clinicOperations = {
  async create(data: Omit<Clinic, 'id' | 'createdAt' | 'updatedAt'>): Promise<Clinic> {
    return prisma.clinic.create({
      data: {
        ...data,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    });
  },

  async findBySlug(slug: string): Promise<Clinic | null> {
    return prisma.clinic.findUnique({
      where: { slug },
      include: {
        doctors: true,
        admin: true,
      },
    });
  },

  async findByCalcomTeamId(calcomTeamId: string): Promise<Clinic | null> {
    return prisma.clinic.findUnique({
      where: { calcomTeamId },
      include: {
        doctors: true,
      },
    });
  },

  async update(id: string, data: Partial<Clinic>): Promise<Clinic> {
    return prisma.clinic.update({
      where: { id },
      data: {
        ...data,
        updatedAt: new Date(),
      },
    });
  },

  async getAll(): Promise<Clinic[]> {
    return prisma.clinic.findMany({
      include: {
        doctors: true,
        admin: true,
      },
    });
  },
};

// Doctor operations
export const doctorOperations = {
  async create(data: Omit<Doctor, 'id' | 'createdAt' | 'updatedAt'>): Promise<Doctor> {
    return prisma.doctor.create({
      data: {
        ...data,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    });
  },

  async findBySlug(slug: string): Promise<Doctor | null> {
    return prisma.doctor.findUnique({
      where: { slug },
      include: {
        clinic: true,
        user: true,
      },
    });
  },

  async findByCalcomUserId(calcomUserId: string): Promise<Doctor | null> {
    return prisma.doctor.findUnique({
      where: { calcomUserId },
      include: {
        clinic: true,
        user: true,
      },
    });
  },

  async findByClinic(clinicId: string): Promise<Doctor[]> {
    return prisma.doctor.findMany({
      where: { clinicId },
      include: {
        user: true,
      },
    });
  },

  async update(id: string, data: Partial<Doctor>): Promise<Doctor> {
    return prisma.doctor.update({
      where: { id },
      data: {
        ...data,
        updatedAt: new Date(),
      },
    });
  },

  async getAll(): Promise<Doctor[]> {
    return prisma.doctor.findMany({
      include: {
        clinic: true,
        user: true,
      },
    });
  },
};

// User operations
export const userOperations = {
  async create(data: Omit<User, 'id' | 'createdAt' | 'updatedAt'>): Promise<User> {
    return prisma.user.create({
      data: {
        ...data,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    });
  },

  async findByEmail(email: string): Promise<User | null> {
    return prisma.user.findUnique({
      where: { email },
    });
  },

  async findByCalcomUserId(calcomUserId: string): Promise<User | null> {
    return prisma.user.findUnique({
      where: { calcomUserId },
    });
  },

  async update(id: string, data: Partial<User>): Promise<User> {
    return prisma.user.update({
      where: { id },
      data: {
        ...data,
        updatedAt: new Date(),
      },
    });
  },
};

// Appointment operations
export const appointmentOperations = {
  async create(data: Omit<Appointment, 'id' | 'createdAt'>): Promise<Appointment> {
    return prisma.appointment.create({
      data: {
        ...data,
        createdAt: new Date(),
      },
      include: {
        doctor: true,
      },
    });
  },

  async findByCalcomBookingId(calcomBookingId: string): Promise<Appointment | null> {
    return prisma.appointment.findUnique({
      where: { calcomBookingId },
      include: {
        doctor: true,
      },
    });
  },

  async findByDoctor(doctorId: string, filters?: {
    status?: string;
    dateFrom?: Date;
    dateTo?: Date;
  }): Promise<Appointment[]> {
    const where: any = { doctorId };

    if (filters?.status) {
      where.status = filters.status;
    }

    if (filters?.dateFrom || filters?.dateTo) {
      where.startTime = {};
      if (filters.dateFrom) where.startTime.gte = filters.dateFrom;
      if (filters.dateTo) where.startTime.lte = filters.dateTo;
    }

    return prisma.appointment.findMany({
      where,
      include: {
        doctor: true,
      },
      orderBy: {
        startTime: 'asc',
      },
    });
  },

  async updateStatus(id: string, status: Appointment['status']): Promise<Appointment> {
    return prisma.appointment.update({
      where: { id },
      data: { status },
      include: {
        doctor: true,
      },
    });
  },
};

// Pending registration operations
export const registrationOperations = {
  async create(data: Omit<PendingRegistration, 'id' | 'submittedAt' | 'status'>): Promise<PendingRegistration> {
    return prisma.pendingRegistration.create({
      data: {
        ...data,
        submittedAt: new Date(),
        status: 'PENDING',
      },
    });
  },

  async findAll(): Promise<PendingRegistration[]> {
    return prisma.pendingRegistration.findMany({
      orderBy: {
        submittedAt: 'desc',
      },
    });
  },

  async updateStatus(id: string, status: 'APPROVED' | 'REJECTED'): Promise<PendingRegistration> {
    const updateData: any = {
      status,
    };

    if (status === 'APPROVED') {
      updateData.approvedAt = new Date();
    } else {
      updateData.rejectedAt = new Date();
    }

    return prisma.pendingRegistration.update({
      where: { id },
      data: updateData,
    });
  },
};